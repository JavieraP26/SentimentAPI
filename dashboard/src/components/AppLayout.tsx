import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Navbar } from './Navbar';

// Layout general con navbar, header y footer
export function AppLayout() {
    return (
        <div>
            <Navbar />
            <div className="bg-glow"></div>
            <div className="container py-5">
                <Header statusText="EN LÍNEA" />
                <Outlet />
                <footer className="text-center mt-5 text-muted-light opacity-50 small">
                    <p>&copy; 2026 SentimentAPI v1.0 - Hackathon ONE II - Latam - H12-25-L-Equipo 63</p>
                </footer>
            </div>
        </div>
    );
}
