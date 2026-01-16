import { NavLink } from 'react-router-dom';

// Barra de navegación principal
export function Navbar() {
    return (
        <nav className="navbar navbar-expand-lg navbar-dark">
            <div className="container">
                <span className="navbar-brand logo-navbar">SentimentAPI</span>
                <button
                    className="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#mainNavbar"
                    aria-controls="mainNavbar"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="mainNavbar">
                    <div className="navbar-nav ms-auto gap-2">
                        <NavLink className="nav-link" to="/">
                            <i className="bi bi-house-door me-1"></i>Inicio
                        </NavLink>
                        <NavLink className="nav-link" to="/historial">
                            <i className="bi bi-bar-chart-line me-1"></i>Historial
                        </NavLink>
                        <NavLink className="nav-link" to="/batch">
                            <i className="bi bi-folder me-1"></i>Batch
                        </NavLink>
                    </div>
                </div>
            </div>
        </nav>
    );
}
