import { useRef, useState } from 'react';
import { analyzeBatch } from '../services/sentimentService';
import { extractTextsFromCsv } from '../utils/csv';
import type { BatchResultItem } from '../types';

type AlertType = 'success' | 'danger';

// Hook para análisis batch por CSV
export function useBatch(onSuccess?: () => Promise<void> | void) {
    const csvInputRef = useRef<HTMLInputElement>(null);
    const [mensaje, setMensaje] = useState<string | null>(null);
    const [mensajeTipo, setMensajeTipo] = useState<AlertType | null>(null);
    const [batchResults, setBatchResults] = useState<BatchResultItem[]>([]);
    const [batchPage, setBatchPage] = useState(0);
    const pageSize = 15;

    const handleCsvSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setMensaje(null);
        setMensajeTipo(null);

        const file = csvInputRef.current?.files?.[0];
        if (!file) {
            setMensaje('Por favor selecciona un archivo.');
            setMensajeTipo('danger');
            return;
        }

        try {
            const content = await file.text();
            const texts = extractTextsFromCsv(content).filter(
                (text) => text.length >= 20 && text.length <= 500
            );

            if (texts.length === 0) {
                setMensaje('No se encontraron textos válidos en el CSV.');
                setMensajeTipo('danger');
                return;
            }

            const batchResponse = await analyzeBatch(texts);
            const limit = Math.min(texts.length, batchResponse.results.length);
            const results: BatchResultItem[] = Array.from({ length: limit }).map((_, index) => ({
                textoOriginal: texts[index],
                prevision: batchResponse.results[index].prevision,
                probabilidad: batchResponse.results[index].probabilidad,
                textoTraducido: batchResponse.results[index].textoTraducido ?? null,
                palabrasClave: batchResponse.results[index].palabrasClave ?? []
            }));

            setBatchResults(results);
            setBatchPage(0);
            setMensaje(`¡Éxito! Se han procesado y guardado ${batchResponse.total} comentarios.`);
            setMensajeTipo('success');
            if (onSuccess) {
                await onSuccess();
            }
        } catch (error) {
            setMensaje('Hubo un error procesando el archivo.');
            setMensajeTipo('danger');
        }
    };

    return {
        csvInputRef,
        mensaje,
        mensajeTipo,
        handleCsvSubmit,
        batchResults,
        batchPage,
        pageSize,
        setBatchPage
    };
}
