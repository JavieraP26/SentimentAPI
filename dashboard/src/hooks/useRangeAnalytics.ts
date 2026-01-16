import { useState } from 'react';
import { exportAnalyticsByRange, fetchAnalyticsByRange } from '../services/analyticsService';
import type { DateRangeAnalyticsResponse } from '../types';

// Hook para consultas por rango, filtros y paginación
export function useRangeAnalytics(pageSize = 15) {
    const [rangeStart, setRangeStart] = useState('');
    const [rangeEnd, setRangeEnd] = useState('');
    const [rangeData, setRangeData] = useState<DateRangeAnalyticsResponse | null>(null);
    const [rangeError, setRangeError] = useState<string | null>(null);
    const [rangeLoading, setRangeLoading] = useState(false);
    const [rangeSentiment, setRangeSentiment] = useState('Todos');
    const [rangePage, setRangePage] = useState(0);

    const sentimentValue = rangeSentiment === 'Todos' ? null : rangeSentiment;

    const formatDateForApi = (value: string) => {
        if (!value) {
            return value;
        }
        if (/^\d{2}-\d{2}-\d{4}$/.test(value)) {
            return value;
        }
        if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
            const [year, month, day] = value.split('-');
            return `${day}-${month}-${year}`;
        }
        return value;
    };

    const handleRangeSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setRangeError(null);

        if (!rangeStart || !rangeEnd) {
            setRangeError('Selecciona fecha inicial y final.');
            return;
        }

        if (rangeStart > rangeEnd) {
            setRangeError('La fecha inicial no puede ser mayor que la final.');
            return;
        }

        try {
            setRangeLoading(true);
            const data = await fetchAnalyticsByRange(
                formatDateForApi(rangeStart),
                formatDateForApi(rangeEnd),
                sentimentValue,
                rangePage,
                pageSize
            );
            setRangeData(data);
        } catch (error) {
            setRangeError('No se pudo cargar la consulta por rango.');
        } finally {
            setRangeLoading(false);
        }
    };

    const handleRangeExport = async () => {
        setRangeError(null);
        if (!rangeStart || !rangeEnd) {
            setRangeError('Selecciona fecha inicial y final.');
            return;
        }
        try {
            const blob = await exportAnalyticsByRange(
                formatDateForApi(rangeStart),
                formatDateForApi(rangeEnd),
                sentimentValue
            );
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `sentiment-range-${rangeStart}-a-${rangeEnd}.csv`;
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            setRangeError('No se pudo exportar el CSV, por favor intente nuevamente.');
        }
    };

    const goToPage = async (nextPage: number) => {
        if (!rangeStart || !rangeEnd) {
            return;
        }
        setRangeLoading(true);
        try {
            const data = await fetchAnalyticsByRange(
                formatDateForApi(rangeStart),
                formatDateForApi(rangeEnd),
                sentimentValue,
                nextPage,
                pageSize
            );
            setRangeData(data);
            setRangePage(nextPage);
        } catch (error) {
            setRangeError('No se pudo cargar la página, por favor intente nuevamente.');
        } finally {
            setRangeLoading(false);
        }
    };

    const handleStartChange = (value: string) => {
        setRangeStart(value);
        setRangePage(0);
    };

    const handleEndChange = (value: string) => {
        setRangeEnd(value);
        setRangePage(0);
    };

    const handleSentimentChange = (value: string) => {
        setRangeSentiment(value);
        setRangePage(0);
    };

    return {
        rangeStart,
        rangeEnd,
        rangeSentiment,
        rangeData,
        rangeError,
        rangeLoading,
        handleRangeSubmit,
        handleRangeExport,
        goToPage,
        handleStartChange,
        handleEndChange,
        handleSentimentChange
    };
}
