import { useState } from 'react';
import { analyzeSentiment } from '../services/sentimentService';
import type { SentimentResponse } from '../types';

type AlertType = 'success' | 'danger';

// Hook para análisis individual
export function useSentiment(onSuccess?: () => Promise<void> | void) {
    const [texto, setTexto] = useState('');
    const [resultado, setResultado] = useState<SentimentResponse | null>(null);
    const [mensaje, setMensaje] = useState<string | null>(null);
    const [mensajeTipo, setMensajeTipo] = useState<AlertType | null>(null);

    const handleSentimentSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setMensaje(null);
        setMensajeTipo(null);

        try {
            const response = await analyzeSentiment(texto);
            setResultado(response);
            if (onSuccess) {
                await onSuccess();
            }
        } catch (error) {
            setMensaje('Hubo un error analizando el texto.');
            setMensajeTipo('danger');
        }
    };

    return {
        texto,
        setTexto,
        resultado,
        mensaje,
        mensajeTipo,
        handleSentimentSubmit
    };
}
