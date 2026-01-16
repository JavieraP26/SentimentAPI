import { STATS_ENDPOINT } from '../config';
import type { StatsResponse } from '../types';

// Obtiene estadísticas del backend
export async function fetchStats(): Promise<StatsResponse> {
    const response = await fetch(STATS_ENDPOINT);
    if (!response.ok) {
        throw new Error('No se pudieron cargar las estadísticas, por favor intente nuevamente.');
    }
    return response.json();
}
