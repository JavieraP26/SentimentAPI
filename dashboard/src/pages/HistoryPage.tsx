import { RangePanel } from '../components/RangePanel';
import { useRangeAnalytics } from '../hooks/useRangeAnalytics';

// Página de historial y consultas por rango
export function HistoryPage() {
    const {
        rangeStart,
        rangeEnd,
        rangeSentiment,
        rangeData,
        rangeError,
        rangeLoading,
        handleRangeSubmit,
        handleRangeExport,
        goToPage,
        handleStartChange,
        handleEndChange,
        handleSentimentChange
    } = useRangeAnalytics();

    return (
        <div className="row g-4">
            <RangePanel
                rangeStart={rangeStart}
                rangeEnd={rangeEnd}
                rangeSentiment={rangeSentiment}
                rangeData={rangeData}
                rangeError={rangeError}
                rangeLoading={rangeLoading}
                onStartChange={handleStartChange}
                onEndChange={handleEndChange}
                onSentimentChange={handleSentimentChange}
                onSubmit={handleRangeSubmit}
                onExport={handleRangeExport}
                onPageChange={goToPage}
            />
        </div>
    );
}
