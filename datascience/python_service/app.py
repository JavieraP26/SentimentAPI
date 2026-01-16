from flask import Flask, request, jsonify
import joblib
import re
import numpy as np
import nltk
from nltk.stem import WordNetLemmatizer
from nltk.corpus import stopwords
from deep_translator import GoogleTranslator

# Descargar recursos necesarios al iniciar
nltk.download('wordnet')
nltk.download('omw-1.4')
nltk.download('stopwords')

app = Flask(__name__)

# Cargar modelos (Asegurarse de que los .pkl estén en esta misma carpeta)
try:
    model = joblib.load('sentiment_model_v1.pkl')
    vectorizer = joblib.load('tfidf_vectorizer_v1.pkl')
    print("✅ Modelos cargados correctamente.")
except Exception as e:
    print(f"❌ Error cargando modelos: {e}")
    print("Recuerda copiar tus archivos .pkl en la carpeta 'python_service'")

lemmatizer = WordNetLemmatizer()
stop_words = set(stopwords.words('english'))

def limpiar_texto(text):
    text = re.sub(r'<br\s*/?>', ' ', text)
    text = re.sub(r'[^a-zA-Z\s]', '', text)
    text = text.lower()
    words = text.split()
    words = [lemmatizer.lemmatize(w) for w in words if w not in stop_words]
    return " ".join(words)
    
# --- FUNCIÓN CENTRAL DE PREDICCIÓN (REUTILIZABLE) ---
def procesar_texto_individual(texto_usuario):
    # 1. Traducción
    try:
        translator = GoogleTranslator(source='auto', target='en')
        texto_traducido = translator.translate(texto_usuario)
    except:
        texto_traducido = texto_usuario

    # 2. Modelo
    texto_limpio = limpiar_texto(texto_traducido)
    vectorizado = vectorizer.transform([texto_limpio])
    
    prediccion_etiqueta = model.predict(vectorizado)[0]
    probabilidades = model.predict_proba(vectorizado)
    confianza = float(np.max(probabilidades))
    
    resultado = "Positivo" if prediccion_etiqueta == 'positive' else "Negativo"
    
    # 3. Explicabilidad
    feature_names = vectorizer.get_feature_names_out()
    indices_palabras = vectorizado.indices
    coeficientes = model.coef_[0]
    
    palabras_influyentes = []
    for idx in indices_palabras:
        palabras_influyentes.append((feature_names[idx], coeficientes[idx]))
    
    top_features = []
    if prediccion_etiqueta == 'positive':
        palabras_influyentes.sort(key=lambda x: x[1], reverse=True)
        top_features = [p[0] for p in palabras_influyentes if p[1] > 0][:5]
    else:
        palabras_influyentes.sort(key=lambda x: x[1])
        top_features = [p[0] for p in palabras_influyentes if p[1] < 0][:5]

    return {
        "sentiment": "Positive" if resultado == "Positivo" else "Negative",
        "probability": round(confianza, 4),
        "translatedText": texto_traducido,
        "topFeatures": top_features
    }

# --- ENDPOINT INDIVIDUAL ---
@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    return jsonify(procesar_texto_individual(data.get('text', '')))

# --- ENDPOINT BATCH ---
@app.route('/predict_batch', methods=['POST'])
def predict_batch():
    data = request.json
    lista_textos = data.get('texts', []) # Recibe lista ["texto1", "texto2"]
    
    resultados = []
    print(f"🔄 Procesando lote de {len(lista_textos)} textos...")
    
    for texto in lista_textos:
        # Procesamos uno por uno (reutilizando la lógica)
        res = procesar_texto_individual(texto)
        resultados.append(res)
        
    return jsonify(resultados) # Devuelve lista de objetos JSON  
    
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8001, debug=True)
