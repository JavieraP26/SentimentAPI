import { ANALYTICS_RANGE_ENDPOINT } from '../config';
import type { DateRangeAnalyticsResponse } from '../types';

// Consulta analytics por rango de fechas
export async function fetchAnalyticsByRange(
    startDate: string,
    endDate: string,
    sentiment: string | null,
    page: number,
    size: number
): Promise<DateRangeAnalyticsResponse> {
    const params = new URLSearchParams({
        startDate,
        endDate,
        page: String(page),
        size: String(size)
    });

    if (sentiment) {
        params.set('sentiment', sentiment);
    }

    const response = await fetch(`${ANALYTICS_RANGE_ENDPOINT}?${params.toString()}`);
    if (!response.ok) {
        throw new Error('No se pudo cargar la consulta por rango, por favor intente nuevamente.');
    }
    return response.json();
}

// Exporta el rango como CSV desde el backend
export async function exportAnalyticsByRange(
    startDate: string,
    endDate: string,
    sentiment: string | null
): Promise<Blob> {
    const params = new URLSearchParams({ startDate, endDate });
    if (sentiment) {
        params.set('sentiment', sentiment);
    }

    const response = await fetch(`${ANALYTICS_RANGE_ENDPOINT}/export?${params.toString()}`);
    if (!response.ok) {
        throw new Error('No se pudo exportar el CSV, por favor intente nuevamente.');
    }
    return response.blob();
}
