import { useCallback, useEffect, useState } from 'react';
import { fetchStats } from '../services/statsService';
import type { StatsResponse } from '../types';

// Hook para cargar estadísticas generales
export function useStats() {
    const [stats, setStats] = useState<StatsResponse | null>(null);

    const loadStats = useCallback(async () => {
        const data = await fetchStats();
        setStats(data);
    }, []);

    useEffect(() => {
        loadStats().catch((error) => {
            console.error('Error cargando estadísticas:', error);
        });
    }, [loadStats]);

    return { stats, loadStats };
}
