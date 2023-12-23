import { useEffect, useRef } from 'react';

const useMatchHeight = (adjustRefName) => {
    const adjustRef = useRef(null);

    const body = document.body;
    const html = document.documentElement;

    const adjustHeight = () => {
        if (adjustRef.current) {
            const targetHeight = Math.max(body.scrollHeight, body.offsetHeight, 
                html.clientHeight, html.scrollHeight, html.offsetHeight);
            adjustRef.current.style.height = `${targetHeight}px`;
        }
    };

    useEffect(() => {
        adjustHeight();
        window.addEventListener('resize', adjustHeight);

        // Cleanup
        return () => {
            window.removeEventListener('resize', adjustHeight);
        };
    }, []);

    return { [adjustRefName]: adjustRef };
};

export default useMatchHeight;
