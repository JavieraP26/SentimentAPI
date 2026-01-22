import { useState } from 'react';
import type { BatchResultItem } from '../types';
import { normalizeCsvText } from '../utils/csv';
import { DetailModal } from './DetailModal';

type BatchResultsPanelProps = {
    results: BatchResultItem[];
    page: number;
    pageSize: number;
    onPageChange: (page: number) => void;
};

// Tabla de resultados batch con paginación y modal
export function BatchResultsPanel({ results, page, pageSize, onPageChange }: BatchResultsPanelProps) {
    if (results.length === 0) return null;

    const totalPages = Math.ceil(results.length / pageSize);
    const startIndex = page * pageSize;
    const pageItems = results.slice(startIndex, startIndex + pageSize);
    const [openIndex, setOpenIndex] = useState<number | null>(null);

    return (
        <div className="col-12 animate-fade-up">
            <div className="card glass-card">
                <div className="card-body p-4">
                    <h5 className="fw-bold text-white mb-3">
                        Resultados batch ({results.length})
                    </h5>
                    <div className="table-responsive">
                        <table className="table table-glass table-striped align-middle">
                            <thead>
                                <tr>
                                    <th>Texto Original</th>
                                    <th>Texto Interpretado</th>
                                    <th>Previsión</th>
                                    <th>Confianza</th>
                                    <th>Palabras clave</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {pageItems.map((item, index) => {
                                    const modalId = `batchDetailModal-${page}-${index}`;
                                    return (
                                        <tr key={modalId}>
                                            <td>{normalizeCsvText(item.textoOriginal)}</td>
                                            <td>{item.textoTraducido ? normalizeCsvText(item.textoTraducido) : '-'}</td>
                                            <td>{item.prevision}</td>
                                            <td>{(item.probabilidad * 100).toFixed(2)}%</td>
                                            <td>{item.palabrasClave?.join(', ') || '-'}</td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="btn btn-sm btn-outline-light"
                                                    onClick={() => setOpenIndex(startIndex + index)}
                                                >
                                                    <i className="bi bi-eye"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>

                    <div className="d-flex justify-content-between align-items-center mt-3">
                        <small className="text-muted-light">
                            Página {page + 1} de {totalPages}
                        </small>
                        <div className="btn-group">
                            <button
                                type="button"
                                className="btn btn-outline-light btn-sm"
                                disabled={page <= 0}
                                onClick={() => onPageChange(page - 1)}
                            >
                                ← Anterior
                            </button>
                            <button
                                type="button"
                                className="btn btn-outline-light btn-sm"
                                disabled={page + 1 >= totalPages}
                                onClick={() => onPageChange(page + 1)}
                            >
                                Siguiente →
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <DetailModal
                isOpen={openIndex !== null}
                title="Detalle del análisis"
                onClose={() => setOpenIndex(null)}
            >
                {openIndex !== null && results[openIndex] && (
                    <>
                        <p><strong>Texto original:</strong> {normalizeCsvText(results[openIndex].textoOriginal)}</p>
                        <p><strong>Texto interpretado:</strong> {results[openIndex].textoTraducido ? normalizeCsvText(results[openIndex].textoTraducido) : '-'}</p>
                        <p><strong>Previsión:</strong> {results[openIndex].prevision}</p>
                        <p><strong>Probabilidad:</strong> {(results[openIndex].probabilidad * 100).toFixed(2)}%</p>
                        <p><strong>Palabras clave:</strong> {results[openIndex].palabrasClave?.join(', ') || '-'}</p>
                    </>
                )}
            </DetailModal>
        </div>
    );
}
