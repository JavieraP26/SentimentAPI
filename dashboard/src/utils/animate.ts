// Animación simple de contador numérico
export function animateValue(
    element: HTMLElement,
    start: number,
    end: number,
    duration: number
): void {
    let startTimestamp: number | null = null;
    const step = (timestamp: number) => {
        if (startTimestamp === null) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        element.innerHTML = Math.floor(progress * (end - start) + start).toString();
        if (progress < 1) window.requestAnimationFrame(step);
    };
    window.requestAnimationFrame(step);
}
