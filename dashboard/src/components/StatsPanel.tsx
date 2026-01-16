import { useEffect, useRef } from 'react';
import { animateValue } from '../utils/animate';
import type { StatsResponse } from '../types';

type StatsPanelProps = {
    stats: StatsResponse | null;
    chartPorcentajesRef: React.RefObject<HTMLCanvasElement | null>;
    chartCantidadesRef: React.RefObject<HTMLCanvasElement | null>;
    chartKeywordsRef: React.RefObject<HTMLCanvasElement | null>;
};

// Panel de métricas generales (charts y total)
export function StatsPanel({
    stats,
    chartPorcentajesRef,
    chartCantidadesRef,
    chartKeywordsRef
}: StatsPanelProps) {
    const labelTotalRef = useRef<HTMLSpanElement>(null);

    useEffect(() => {
        if (!stats || !labelTotalRef.current) return;
        animateValue(labelTotalRef.current, 0, stats.total_analizados, 1000);
    }, [stats]);

    return (
        <div className="col-lg-5 animate-fade-left">
            <div className="card glass-card h-100">
                <div className="card-header border-0 bg-transparent pt-4 px-4">
                    <h4 className="card-title fw-bold text-white">
                        <i className="bi bi-graph-up-arrow text-accent me-2"></i>
                        Métricas Generales en Tiempo Real
                    </h4>
                </div>
                <div className="card-body px-4">
                    <div className="text-center mb-4">
                        <h6 className="text-muted-light text-uppercase tracking-wider">Total Analizados</h6>
                        <h2 className="display-3 fw-bold text-white" id="labelTotal">
                            <span ref={labelTotalRef}>0</span>
                        </h2>
                    </div>

                    <div className="chart-wrapper mb-3">
                        <canvas ref={chartPorcentajesRef}></canvas>
                    </div>
                    <div className="chart-wrapper-mini mx-auto mb-4">
                        <canvas ref={chartCantidadesRef}></canvas>
                    </div>

                    <hr className="border-secondary opacity-25" />
                    <h6 className="text-muted-light small text-uppercase mb-3 mt-3 text-center">
                        Top Palabras Clave Detectadas
                    </h6>
                    <div className="chart-wrapper" style={{ height: '250px' }}>
                        <canvas ref={chartKeywordsRef}></canvas>
                    </div>
                </div>
            </div>
        </div>
    );
}
