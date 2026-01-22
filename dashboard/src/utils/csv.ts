// Extrae la primera columna del CSV y devuelve una lista de textos
export function extractTextsFromCsv(content: string): string[] {
    const lines = content.split(/\r?\n/);
    const texts: string[] = [];

    let firstLine = true;
    for (const line of lines) {
        const raw = line.trim();
        if (!raw) continue;

        if (firstLine && looksLikeHeader(raw)) {
            firstLine = false;
            continue;
        }
        firstLine = false;

        const text = extractFirstColumn(raw);
        if (text) {
            texts.push(text);
        }
    }

    return texts;
}

function looksLikeHeader(line: string): boolean {
    const lower = line.toLowerCase();
    return (
        lower.includes('text') ||
        lower.includes('texto') ||
        lower.includes('feedback') ||
        lower.includes('comentario') ||
        lower.includes('comentarios') ||
        lower.includes('opinion') ||
        lower.includes('opiniones') ||
        lower.includes('mensaje') ||
        lower.includes('mensajes')
    );
}

function extractFirstColumn(line: string): string {
    const firstField = parseFirstCsvField(line);
    return normalizeCsvText(firstField);
}

export function normalizeCsvText(value: string): string {
    const trimmed = value.trim();
    if (!trimmed) return trimmed;
    const unescaped = trimmed.replace(/""/g, '"');
    if (unescaped.startsWith('"') && unescaped.endsWith('"') && unescaped.length >= 2) {
        return unescaped.substring(1, unescaped.length - 1).trim();
    }
    return unescaped.trim();
}

function parseFirstCsvField(line: string): string {
    let inQuotes = false;
    let result = '';

    for (let i = 0; i < line.length; i += 1) {
        const char = line[i];
        if (char === '"') {
            const next = line[i + 1];
            if (inQuotes && next === '"') {
                result += '"';
                i += 1;
                continue;
            }
            inQuotes = !inQuotes;
            continue;
        }
        if (!inQuotes && char === ',') {
            break;
        }
        result += char;
    }

    return result.trim();
}
