import { useEffect, useRef } from 'react';
import {
    ArcElement,
    BarController,
    BarElement,
    CategoryScale,
    Chart,
    DoughnutController,
    Legend,
    LinearScale,
    Tooltip
} from 'chart.js';

import { useSentiment } from '../hooks/useSentiment';
import { useStats } from '../hooks/useStats';
import { SentimentCard } from '../components/SentimentCard';
import { StatsPanel } from '../components/StatsPanel';
import { AlertMessage } from '../components/AlertMessage';

Chart.register(
    BarController,
    BarElement,
    CategoryScale,
    LinearScale,
    Tooltip,
    Legend,
    DoughnutController,
    ArcElement
);

// Página principal con análisis individual y métricas
export function HomePage() {
    const { stats, loadStats } = useStats();
    const {
        texto,
        setTexto,
        resultado,
        mensaje,
        mensajeTipo,
        handleSentimentSubmit
    } = useSentiment(loadStats);

    const chartPorcentajesRef = useRef<HTMLCanvasElement>(null);
    const chartCantidadesRef = useRef<HTMLCanvasElement>(null);
    const chartKeywordsRef = useRef<HTMLCanvasElement>(null);
    const chartsRef = useRef<{
        porcentajes?: Chart;
        cantidades?: Chart;
        keywords?: Chart;
    }>({});

    useEffect(() => {
        if (!stats) return;

        const porcPos = parseFloat(stats.porcentaje_positivos.replace('%', ''));
        const porcNeg = parseFloat(stats.porcentaje_negativos.replace('%', ''));
        const wordsPos = stats.top_words_pos || {};
        const wordsNeg = stats.top_words_neg || {};

        if (chartPorcentajesRef.current) {
            chartsRef.current.porcentajes?.destroy();
            chartsRef.current.porcentajes = new Chart(chartPorcentajesRef.current.getContext('2d')!, {
                type: 'bar',
                data: {
                    labels: ['Positivo', 'Negativo'],
                    datasets: [{
                        label: 'Porcentaje',
                        data: [porcPos, porcNeg],
                        backgroundColor: ['rgba(74, 222, 128, 0.7)', 'rgba(248, 113, 113, 0.7)'],
                        borderColor: ['#4ade80', '#f87171'],
                        borderWidth: 1,
                        borderRadius: 5
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        x: { grid: { color: 'rgba(255,255,255,0.05)' } },
                        y: { grid: { display: false } }
                    }
                }
            });
        }

        if (chartCantidadesRef.current) {
            chartsRef.current.cantidades?.destroy();
            chartsRef.current.cantidades = new Chart(chartCantidadesRef.current.getContext('2d')!, {
                type: 'doughnut',
                data: {
                    labels: ['Positivos', 'Negativos'],
                    datasets: [{
                        data: [stats.positivos, stats.negativos],
                        backgroundColor: ['#4ade80', '#f87171'],
                        borderWidth: 0,
                        hoverOffset: 4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '70%',
                    plugins: { legend: { display: false } }
                }
            });
        }

        if (chartKeywordsRef.current) {
            chartsRef.current.keywords?.destroy();

            const labels = [...Object.keys(wordsPos), ...Object.keys(wordsNeg)];
            const dataValues: number[] = [];
            const backgroundColors: string[] = [];
            const borderColors: string[] = [];

            Object.keys(wordsPos).forEach((key) => {
                dataValues.push(wordsPos[key]);
                backgroundColors.push('rgba(74, 222, 128, 0.6)');
                borderColors.push('#4ade80');
            });

            Object.keys(wordsNeg).forEach((key) => {
                dataValues.push(wordsNeg[key]);
                backgroundColors.push('rgba(248, 113, 113, 0.6)');
                borderColors.push('#f87171');
            });

            chartsRef.current.keywords = new Chart(chartKeywordsRef.current.getContext('2d')!, {
                type: 'bar',
                data: {
                    labels,
                    datasets: [{
                        label: 'Frecuencia',
                        data: dataValues,
                        backgroundColor: backgroundColors,
                        borderColor: borderColors,
                        borderWidth: 1,
                        borderRadius: 4
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return 'Frecuencia: ' + context.raw;
                                }
                            }
                        }
                    },
                    scales: {
                        x: {
                            grid: { color: 'rgba(255,255,255,0.05)' },
                            ticks: { stepSize: 1 }
                        },
                        y: { grid: { display: false } }
                    }
                }
            });
        }
    }, [stats]);

    return (
        <div className="row g-4">
            <SentimentCard
                texto={texto}
                resultado={resultado}
                onSubmit={handleSentimentSubmit}
                onTextChange={setTexto}
            />

            <StatsPanel
                stats={stats}
                chartPorcentajesRef={chartPorcentajesRef}
                chartCantidadesRef={chartCantidadesRef}
                chartKeywordsRef={chartKeywordsRef}
            />

            <AlertMessage message={mensaje} type={mensajeTipo ?? 'info'} />
        </div>
    );
}
