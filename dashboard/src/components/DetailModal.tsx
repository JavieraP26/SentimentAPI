type DetailModalProps = {
    isOpen: boolean;
    title: string;
    onClose: () => void;
    children: React.ReactNode;
};

// Modal simple
export function DetailModal({ isOpen, title, onClose, children }: DetailModalProps) {
    if (!isOpen) return null;

    return (
        <div className="modal-overlay" role="dialog" aria-modal="true">
            <div className="modal-panel">
                <div className="modal-header-custom">
                    <h5 className="modal-title">{title}</h5>
                    <button type="button" className="btn-close btn-close-white" onClick={onClose}></button>
                </div>
                <div className="modal-body-custom">{children}</div>
            </div>
            <button type="button" className="modal-backdrop" onClick={onClose} aria-label="Cerrar modal"></button>
        </div>
    );
}
