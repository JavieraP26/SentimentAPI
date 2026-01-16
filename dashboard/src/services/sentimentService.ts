import { BATCH_ENDPOINT, SENTIMENT_ENDPOINT } from '../config';
import type { BatchResponse, SentimentResponse } from '../types';

// Análisis individual
export async function analyzeSentiment(text: string): Promise<SentimentResponse> {
    const response = await fetch(SENTIMENT_ENDPOINT, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text })
    });

    if (!response.ok) {
        throw new Error('No se pudo analizar el texto, por favor intente nuevamente.');
    }

    return response.json();
}

// Análisis batch
export async function analyzeBatch(texts: string[]): Promise<BatchResponse> {
    const response = await fetch(BATCH_ENDPOINT, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ texts })
    });

    if (!response.ok) {
        throw new Error('No se pudo procesar el batch, por favor intente nuevamente.');
    }

    return response.json();
}
