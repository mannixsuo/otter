package character


val CharacterSet = buildMap {
    put('0', buildMap {
        put('`', '\u25c6') // '◆'
        put('a', '\u2592') // '▒'
        put('b', '\u2409') // '␉' (HT)
        put('c', '\u240c') // '␌' (FF)
        put('d', '\u240d') // '␍' (CR)
        put('e', '\u240a') // '␊' (LF)
        put('f', '\u00b0') // '°'
        put('g', '\u00b1') // '±'
        put('h', '\u2424') // '␤' (NL)
        put('i', '\u240b') // '␋' (VT)
        put('j', '\u2518') // '┘'
        put('k', '\u2510') // '┐'
        put('l', '\u250c')// '┌'
        put('m', '\u2514') // '└'
        put('n', '\u253c') // '┼'
        put('o', '\u23ba') // '⎺'
        put('p', '\u23bb') // '⎻'
        put('q', '\u2500') // '─'
        put('r', '\u23bc') // '⎼'
        put('s', '\u23bd') // '⎽'
        put('t', '\u251c') // '├'
        put('u', '\u2524')// '┤'
        put('v', '\u2534') // '┴'
        put('w', '\u252c') // '┬'
        put('x', '\u2502') // '│'
        put('y', '\u2264') // '≤'
        put('z', '\u2265') // '≥'
        put('{', '\u03c0') // 'π'
        put('|', '\u2260') // '≠'
        put('}', '\u00a3') // '£'
        put('~', '\u00b7')  // '·'
    })

    put('A', buildMap {
        put('#', '£')
    })
}