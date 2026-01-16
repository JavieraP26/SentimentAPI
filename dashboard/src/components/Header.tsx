type HeaderProps = {
    statusText: string;
};

// Encabezado principal del dashboard
export function Header({ statusText }: HeaderProps) {
    return (
        <header className="text-center mb-5 animate-fade-down">
            <h1 className="display-4 fw-bold text-gradient">SentimentAPI</h1>
            <p className="lead text-muted-light">Análisis de Sentimientos de Feedbacks</p>
            <p className="text-muted-light small mb-0">
                <i className="bi bi-circle-fill text-success me-1" style={{ fontSize: '8px' }}></i>
                Sistema: <span className="text-white fw-bold">{statusText}</span>
            </p>
        </header>
    );
}
