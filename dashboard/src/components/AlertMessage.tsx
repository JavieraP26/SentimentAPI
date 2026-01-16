type AlertMessageProps = {
    message: string | null;
    type: 'success' | 'danger' | 'info';
};

// Alerta reutilizable para feedback del usuario
export function AlertMessage({ message, type }: AlertMessageProps) {
    if (!message) return null;

    return (
        <div className="col-12 mt-2">
            <div className={`alert glass-alert d-flex align-items-center alert-${type}`} role="alert">
                <i className="bi bi-info-circle-fill me-2 fs-5"></i>
                <div>{message}</div>
            </div>
        </div>
    );
}
