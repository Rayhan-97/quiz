import { useContext, useEffect } from 'react';
import ThemeContext, { ThemeData } from '../context/ThemeContext';

const setBodyThemeColour = () => {
    const backgroundColor = window
        .getComputedStyle(document.getElementById('theme-wrapper') as HTMLElement)
        .getPropertyValue('--bg');
    const foregroundColor = window
        .getComputedStyle(document.getElementById('theme-wrapper') as HTMLElement)
        .getPropertyValue('--fg');
    document.body.style.backgroundColor = backgroundColor;
    document.body.style.color = foregroundColor;
};

export type ThemeState = {
    theme: ThemeData;
    setTheme: (theme: ThemeData) => void;
};

const useTheme = (): ThemeState => {
    const themeContext = useContext(ThemeContext);

    useEffect(() => {
        if (themeContext) {
            localStorage.setItem('theme', themeContext?.theme);
        }

        setBodyThemeColour();
    }, [themeContext?.theme]);

    if (themeContext === undefined) {
        throw new Error('Expected ThemeProvider somewhere in the React tree to set context value');
    }


    return {
        theme: themeContext.theme,
        setTheme: (theme: ThemeData) => themeContext.setTheme(theme)
    };
};

export default useTheme;
