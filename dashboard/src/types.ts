export type StatsResponse = {
    total_analizados: number;
    positivos: number;
    negativos: number;
    porcentaje_positivos: string;
    porcentaje_negativos: string;
    top_words_pos: Record<string, number>;
    top_words_neg: Record<string, number>;
};

export type SentimentResponse = {
    prevision: string;
    probabilidad: number;
    textoTraducido?: string | null;
    palabrasClave?: string[] | null;
};

export type BatchResponse = {
    total: number;
    results: SentimentResponse[];
};

export type BatchResultItem = SentimentResponse & {
    textoOriginal: string;
};

export type SentimentRecord = {
    fecha: string;
    textoOriginal: string;
    textoInterpretado: string | null;
    prevision: string;
    probabilidad: number;
    palabrasClave: string[];
};

export type DateRangeAnalyticsResponse = {
    desde: string;
    hasta: string;
    total: number;
    positivos: number;
    negativos: number;
    porcentajePositivos: string;
    porcentajeNegativos: string;
    topWordsPos: Record<string, number>;
    topWordsNeg: Record<string, number>;
    registros: SentimentRecord[];
    page: number;
    size: number;
    totalPages: number;
};