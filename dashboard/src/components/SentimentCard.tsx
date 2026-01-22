import type { SentimentResponse } from '../types';
import felizImg from '../assets/feliz.png';
import tristeImg from '../assets/triste.png';

type SentimentCardProps = {
    texto: string;
    resultado: SentimentResponse | null;
    onSubmit: (event: React.FormEvent) => void;
    onTextChange: (value: string) => void;
};

// Formulario de análisis individual y resultado
export function SentimentCard({ texto, resultado, onSubmit, onTextChange }: SentimentCardProps) {
    const resultadoLower = resultado?.prevision?.toLowerCase() ?? '';
    const probabilidad = resultado ? `${(resultado.probabilidad * 100).toFixed(2)}%` : '';
    const textoInterpretado = resultado?.textoTraducido ?? '';
    const showTranslate = textoInterpretado && textoInterpretado !== texto;
    const palabrasClave = resultado?.palabrasClave ?? [];

    return (
        <div className="col-lg-7 animate-fade-right">
            <div className="card glass-card h-100">
                <div className="card-header border-0 bg-transparent pt-4 px-4">
                    <h4 className="card-title fw-bold text-white">
                        <i className="bi bi-chat-square-quote-fill text-accent me-2"></i>
                        Análisis individual de feedbacks
                    </h4>
                </div>
                <div className="card-body px-4">
                    <div className="alert alert-dark-custom mb-4">
                        <ul className="mb-0 small text-muted-light ps-3">
                            <li>Introduce tu feedback en el idioma que gustes, el sistema lo detectará automáticamente.</li>
                            <li>La longitud del texto debe ser preferiblemente entre 10 a 500 caracteres.</li>
                            <li>Es recomendable no utilizar caracteres especiales.</li>
                        </ul>
                    </div>
                    <form onSubmit={onSubmit}>
                        <div className="mb-4">
                            <textarea
                                className="form-control input-tech"
                                id="texto"
                                name="texto"
                                rows={5}
                                placeholder="Escribe aquí el comentario del cliente..."
                                required
                                value={texto}
                                onChange={(event) => onTextChange(event.target.value)}
                            />
                        </div>
                        <div className="d-grid">
                            <button type="submit" className="btn btn-tech btn-lg">
                                <i className="bi bi-lightning-charge-fill me-2"></i>ANALIZAR AHORA
                            </button>
                        </div>
                    </form>

                    {resultado && (
                        <div
                            className={`mt-4 result-box p-3 rounded-3 border-start border-4 ${
                                resultadoLower === 'positivo'
                                    ? 'border-success bg-success-soft'
                                    : 'border-danger bg-danger-soft'
                            }`}
                        >
                            <div className="d-flex justify-content-between align-items-center mb-2">
                                <h4
                                    className={`fw-bold mb-0 ${
                                        resultadoLower === 'positivo' ? 'text-success-light' : 'text-danger-light'
                                    }`}
                                >
                                    {resultado.prevision}
                                </h4>
                                <span className="badge bg-dark text-white border border-secondary">
                                    Confianza: <span>{probabilidad}</span>
                                </span>
                            </div>

                            <hr className="border-secondary opacity-25" />

                            <p className="mb-1 text-muted-light small">Feedback Original:</p>
                            <p className="fst-italic text-white mb-3">"{texto}"</p>

                            {showTranslate && (
                                <div className="p-2 rounded bg-dark mb-3">
                                    <p className="mb-0 small text-accent">
                                        <i className="bi bi-translate me-1"></i> Interpretado (EN):{' '}
                                        <span className="text-white fw-normal">{textoInterpretado}</span>
                                    </p>
                                </div>
                            )}

                            {palabrasClave.length > 0 && (
                                <div>
                                    <p className="mb-1 text-muted-light small">Top Features detectadas:</p>
                                    <div>
                                        {palabrasClave.map((palabra) => (
                                            <span key={palabra} className="badge badge-feature me-1 mb-1">
                                                {palabra}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                            )}

                            <div className="text-center mt-4 pt-2 border-top border-secondary border-opacity-25">
                                {resultadoLower === 'positivo' && (
                                    <img
                                        src={felizImg}
                                        alt="Cliente Feliz"
                                        style={{
                                            width: '150px',
                                            height: '150px',
                                            objectFit: 'contain',
                                            filter: 'drop-shadow(0 0 10px rgba(74, 222, 128, 0.5))'
                                        }}
                                        className="animate-fade-up"
                                    />
                                )}

                                {resultadoLower === 'negativo' && (
                                    <img
                                        src={tristeImg}
                                        alt="Cliente Triste"
                                        style={{
                                            width: '150px',
                                            height: '150px',
                                            objectFit: 'contain',
                                            filter: 'drop-shadow(0 0 10px rgba(248, 113, 113, 0.5))'
                                        }}
                                        className="animate-fade-up"
                                    />
                                )}
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
