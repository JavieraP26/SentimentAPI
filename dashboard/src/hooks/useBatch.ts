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
    const [isLoading, setIsLoading] = useState(false);
    const pageSize = 15;

    const readCsvContent = async (file: File): Promise<string> => {
        const buffer = await file.arrayBuffer();
        try {
            const utf8Decoder = new TextDecoder('utf-8', { fatal: true });
            return utf8Decoder.decode(buffer);
        } catch {
            const latinDecoder = new TextDecoder('windows-1252', { fatal: false });
            return latinDecoder.decode(buffer);
        }
    };

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
            setIsLoading(true);
            const content = await readCsvContent(file);
            const texts = extractTextsFromCsv(content).filter(
                (text) => text.length >= 10 && text.length <= 500
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
        } finally {
            setIsLoading(false);
        }
    };

    return {
        csvInputRef,
        mensaje,
        mensajeTipo,
        handleCsvSubmit,
        isLoading,
        batchResults,
        batchPage,
        pageSize,
        setBatchPage
    };
}
