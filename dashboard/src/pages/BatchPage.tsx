import { BatchCard } from '../components/BatchCard';
import { AlertMessage } from '../components/AlertMessage';
import { BatchResultsPanel } from '../components/BatchResultsPanel';
import { useBatch } from '../hooks/useBatch';

// Página de análisis por CSV
export function BatchPage() {
    const {
        csvInputRef,
        mensaje,
        mensajeTipo,
        handleCsvSubmit,
        isLoading,
        batchResults,
        batchPage,
        pageSize,
        setBatchPage
    } = useBatch();

    return (
        <div className="row g-4">
            <BatchCard onSubmit={handleCsvSubmit} fileInputRef={csvInputRef} isLoading={isLoading} />
            <AlertMessage message={mensaje} type={mensajeTipo ?? 'info'} />
            <BatchResultsPanel
                results={batchResults}
                page={batchPage}
                pageSize={pageSize}
                onPageChange={setBatchPage}
            />
        </div>
    );
}
