type BatchCardProps = {
    onSubmit: (event: React.FormEvent) => void;
    fileInputRef: React.RefObject<HTMLInputElement | null>;
};

// Tarjeta de carga batch por CSV
export function BatchCard({ onSubmit, fileInputRef }: BatchCardProps) {
    return (
        <div className="col-12 animate-fade-up">
            <div className="card glass-card">
                <div className="card-body p-4 d-flex flex-column flex-md-row align-items-center gap-4">
                    <div className="flex-shrink-0 text-center text-md-start">
                        <div className="icon-square bg-dark-gradient text-accent rounded-3 mb-2">
                            <i className="bi bi-database-fill-up fs-2"></i>
                        </div>
                    </div>
                    <div className="flex-grow-1">
                        <h4 className="fw-bold text-white">Análisis masivo de feedbacks (CSV)</h4>
                        <ul className="mb-0 small text-muted-light list-unstyled mt-2">
                            <li><i className="bi bi-check2 text-accent me-1"></i> Sube tu archivo CSV con los feedbacks que desees analizar.</li>
                            <li><i className="bi bi-check2 text-accent me-1"></i> El peso máximo del archivo se recomienda que sea de 10MB para un procesamiento más eficiente.</li>
                            <li><i className="bi bi-check2 text-accent me-1"></i> Los feedback deben estar en la primera columna entre comillas dobles (").</li>
                            <li><i className="bi bi-check2 text-accent me-1"></i> Es recomendable no utilizar caracteres especiales.</li>
                        </ul>
                    </div>
                    <div className="flex-shrink-0 w-md-25">
                        <form onSubmit={onSubmit}>
                            <div className="input-group">
                                <input
                                    type="file"
                                    ref={fileInputRef}
                                    className="form-control form-control-sm bg-dark text-light border-secondary"
                                    accept=".csv"
                                    required
                                />
                                <button type="submit" className="btn btn-outline-accent">
                                    <i className="bi bi-upload"></i>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}
