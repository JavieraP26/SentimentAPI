import { Routes, Route } from 'react-router-dom';
import { AppLayout } from './components/AppLayout';
import { HomePage } from './pages/HomePage';
import { HistoryPage } from './pages/HistoryPage';
import { BatchPage } from './pages/BatchPage';

export default function App() {
    return (
        <Routes>
            <Route element={<AppLayout />}>
                <Route path="/" element={<HomePage />} />
                <Route path="/historial" element={<HistoryPage />} />
                <Route path="/batch" element={<BatchPage />} />
            </Route>
        </Routes>
    );
}
