import { ICrossContext } from '@/types/types';
import { createContext, useCallback, useContext, useEffect, useMemo } from 'react';
import { useLocalStorage } from 'usehooks-ts';
import { useRouter } from 'next/router';
import uuid from 'react-uuid';

const CrossContext = createContext(null);
const useCrossContext = () => useContext<ICrossContext>(CrossContext);

const CrossContextProvider = ({ children }) => {
    const id = useMemo(() => uuid(), []);
    const [reloadTrigger, setReloadTrigger] = useLocalStorage('reload-trigger', false);
    const [reloadSource, setReloadSource] = useLocalStorage('reload-source', '');
    const { reload } = useRouter();

    const triggerReload = useCallback(() => {
        setReloadSource(id);
        setReloadTrigger(true);
    }, [setReloadTrigger, setReloadSource]);

    useEffect(() => {
        setReloadTrigger(false);
    }, [setReloadTrigger]);

    useEffect(() => {
        // this is invoked every time the trigger changes
        // when the trigger value is true and the we're on a different tab then the source, refresh it

        if (reloadTrigger && id !== reloadSource) {
            const intervalId = setInterval(() => {
                reload();
            }, 100 + Math.random() * 250);

            return () => {
                clearInterval(intervalId);
            };
        }
    }, [setReloadTrigger, reloadTrigger]);

    const provided = useMemo<ICrossContext>(
        () => ({
            triggerReload,
        }),
        [triggerReload]
    );

    return <CrossContext.Provider value={provided}>{children}</CrossContext.Provider>;
};

export { CrossContext, useCrossContext, CrossContextProvider as default };
