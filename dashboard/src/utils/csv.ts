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
    return lower.includes('text') || lower.includes('feedback') || lower.includes('coment');
}

function extractFirstColumn(line: string): string {
    const commaIndex = line.indexOf(',');
    let first = commaIndex >= 0 ? line.substring(0, commaIndex) : line;

    let trimmed = first.trim();
    if (trimmed.startsWith('"') && trimmed.endsWith('"') && trimmed.length >= 2) {
        trimmed = trimmed.substring(1, trimmed.length - 1).trim();
    }
    return trimmed;
}
