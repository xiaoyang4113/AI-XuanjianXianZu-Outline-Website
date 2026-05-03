/** @type {import('tailwindcss').Config} */
export default {
    content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
    theme: {
        extend: {
            colors: {
                xuan: {
                    bg:     '#0a0a0a',
                    card:   '#141414',
                    border: '#252525',
                    gold:   '#c9a96e',
                    goldLight: '#dfc08a',
                    text:   '#e8e8e8',
                    muted:  '#777777',
                    tagBg:  '#1a2318',
                    tagBorder: '#2d4028',
                }
            },
            fontFamily: {
                sans: ['"Noto Sans SC"', '"Source Han Sans CN"', 'system-ui', 'sans-serif'],
            }
        }
    },
    plugins: []
}