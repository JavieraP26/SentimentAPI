import { useState } from 'react';
import type { DateRangeAnalyticsResponse } from '../types';
import { DetailModal } from './DetailModal';

type RangePanelProps = {
    rangeStart: string;
    rangeEnd: string;
    rangeSentiment: string;
    rangeData: DateRangeAnalyticsResponse | null;
    rangeError: string | null;
    rangeLoading: boolean;
    onStartChange: (value: string) => void;
    onEndChange: (value: string) => void;
    onSentimentChange: (value: string) => void;
    onSubmit: (event: React.FormEvent) => void;
    onExport: () => void;
    onPageChange: (nextPage: number) => void;
};

// Panel de consultas por rango con tabla, resumen y paginación
export function RangePanel({
    rangeStart,
    rangeEnd,
    rangeSentiment,
    rangeData,
    rangeError,
    rangeLoading,
    onStartChange,
    onEndChange,
    onSentimentChange,
    onSubmit,
    onExport,
    onPageChange
}: RangePanelProps) {
    const [openIndex, setOpenIndex] = useState<number | null>(null);
    return (
        <div className="col-12 animate-fade-up">
            <div className="card glass-card">
                <div className="card-body p-4">
                    <div className="d-flex flex-column flex-md-row align-items-start align-items-md-center gap-3 mb-3">
                        <div className="flex-grow-1">
                            <h4 className="fw-bold text-white mb-1">
                                <i className="bi bi-calendar-range text-accent me-2"></i>
                                Consultas por rango de fechas
                            </h4>
                            <p className="text-muted-light mb-0 small">
                                Filtra registros por fecha y sentimiento seleccionado.
                            </p>
                        </div>
                    </div>

                    <form onSubmit={onSubmit} className="row g-3 align-items-end">
                        <div className="col-md-4">
                            <label className="form-label text-muted-light">Fecha inicial</label>
                            <input
                                type="date"
                                className="form-control input-tech"
                                value={rangeStart}
                                onChange={(event) => onStartChange(event.target.value)}
                                required
                            />
                        </div>
                        <div className="col-md-4">
                            <label className="form-label text-muted-light">Fecha final</label>
                            <input
                                type="date"
                                className="form-control input-tech"
                                value={rangeEnd}
                                onChange={(event) => onEndChange(event.target.value)}
                                required
                            />
                        </div>
                        <div className="col-md-4">
                            <label className="form-label text-muted-light">Sentimiento</label>
                            <select
                                className="form-select input-tech"
                                value={rangeSentiment}
                                onChange={(event) => onSentimentChange(event.target.value)}
                            >
                                <option value="Todos">Todos</option>
                                <option value="Positivo">Positivo</option>
                                <option value="Negativo">Negativo</option>
                            </select>
                        </div>
                        <div className="col-md-4">
                            <button type="submit" className="btn btn-tech w-100">
                                <i className="bi bi-search me-2"></i>Consultar
                            </button>
                        </div>
                        <div className="col-md-4">
                            <button type="button" className="btn btn-outline-accent w-100" onClick={onExport}>
                                <i className="bi bi-download me-2"></i>Exportar CSV
                            </button>
                        </div>
                    </form>

                    {rangeError && (
                        <div className="alert alert-danger mt-3">{rangeError}</div>
                    )}

                    {rangeLoading && (
                        <div className="alert alert-dark-custom mt-3">Consultando...</div>
                    )}

                    {rangeData && (
                        <div className="mt-4">
                            <div className="table-responsive">
                                <table className="table table-glass table-striped align-middle">
                                    <thead>
                                        <tr>
                                            <th>Fecha</th>
                                            <th>Texto Original</th>
                                            <th>Texto Interpretado</th>
                                            <th>Previsión</th>
                                            <th>Confianza</th>
                                            <th>Palabras clave</th>
                                            <th>Acciones</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {rangeData.registros.length === 0 && (
                                            <tr>
                                                <td colSpan={7} className="text-center text-muted-light">
                                                    No hay registros en el rango seleccionado.
                                                </td>
                                            </tr>
                                        )}
                                        {rangeData.registros.map((registro, index) => (
                                            <tr key={`${registro.fecha}-${index}`}>
                                                <td>{registro.fecha}</td>
                                                <td>{registro.textoOriginal}</td>
                                                <td>{registro.textoInterpretado || '-'}</td>
                                                <td>{registro.prevision}</td>
                                                <td>{(registro.probabilidad * 100).toFixed(2)}%</td>
                                                <td>{registro.palabrasClave?.join(', ') || '-'}</td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="btn btn-sm btn-outline-light"
                                                    onClick={() => setOpenIndex(index)}
                                                >
                                                    <i className="bi bi-eye"></i>
                                                </button>
                                            </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>

                            <div className="row g-3 mt-2">
                                <div className="col-md-3">
                                    <div className="p-3 rounded bg-dark">
                                        <p className="mb-1 text-muted-light small">Total</p>
                                        <h5 className="mb-0 text-white">{rangeData.total}</h5>
                                    </div>
                                </div>
                                <div className="col-md-3">
                                    <div className="p-3 rounded bg-dark">
                                        <p className="mb-1 text-muted-light small">Positivos</p>
                                        <h5 className="mb-0 text-success-light">
                                            {rangeData.positivos} ({rangeData.porcentajePositivos})
                                        </h5>
                                    </div>
                                </div>
                                <div className="col-md-3">
                                    <div className="p-3 rounded bg-dark">
                                        <p className="mb-1 text-muted-light small">Negativos</p>
                                        <h5 className="mb-0 text-danger-light">
                                            {rangeData.negativos} ({rangeData.porcentajeNegativos})
                                        </h5>
                                    </div>
                                </div>
                                <div className="col-md-3">
                                    <div className="p-3 rounded bg-dark">
                                        <p className="mb-1 text-muted-light small">Top Keywords</p>
                                        <p className="mb-0 text-white small">
                                            {Object.keys(rangeData.topWordsPos).slice(0, 3).join(', ') || 'N/A'}
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <div className="d-flex justify-content-between align-items-center mt-4">
                                <small className="text-muted-light">
                                    Página {rangeData.page + 1} de {rangeData.totalPages} · {rangeData.total} resultados
                                </small>
                                <div className="btn-group">
                                    <button
                                        type="button"
                                        className="btn btn-outline-light btn-sm"
                                        disabled={rangeData.page <= 0 || rangeLoading}
                                        onClick={() => onPageChange(rangeData.page - 1)}
                                    >
                                        ← Anterior
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn-outline-light btn-sm"
                                        disabled={rangeData.page + 1 >= rangeData.totalPages || rangeLoading}
                                        onClick={() => onPageChange(rangeData.page + 1)}
                                    >
                                        Siguiente →
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    <DetailModal
                        isOpen={openIndex !== null}
                        title="Detalle del análisis"
                        onClose={() => setOpenIndex(null)}
                    >
                        {openIndex !== null && rangeData?.registros[openIndex] && (
                            <>
                                <p><strong>Fecha:</strong> {rangeData.registros[openIndex].fecha}</p>
                                <p><strong>Texto original:</strong> {rangeData.registros[openIndex].textoOriginal}</p>
                                <p><strong>Texto interpretado:</strong> {rangeData.registros[openIndex].textoInterpretado || '-'}</p>
                                <p><strong>Previsión:</strong> {rangeData.registros[openIndex].prevision}</p>
                                <p><strong>Probabilidad:</strong> {(rangeData.registros[openIndex].probabilidad * 100).toFixed(2)}%</p>
                                <p><strong>Palabras clave:</strong> {rangeData.registros[openIndex].palabrasClave?.join(', ') || '-'}</p>
                            </>
                        )}
                    </DetailModal>
                </div>
            </div>
        </div>
    );
}
