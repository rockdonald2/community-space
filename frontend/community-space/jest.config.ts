import type { Config } from '@jest/types';

// eslint-disable-next-line import/no-anonymous-default-export
export default async (): Promise<Config.InitialOptions> => {
    return {
        preset: 'ts-jest',

        clearMocks: true,

        setupFilesAfterEnv: [],

        passWithNoTests: true,

        coverageReporters: ['lcov'],
        collectCoverage: true,
        collectCoverageFrom: [
            '**/*.tsx',
            '**/*.ts',
            '!node_modules/',
            '!coverage/',
            '!dist/',
            '!**/*.d.ts',
            '!**/*.config.{js,ts}',
        ],
        coverageThreshold: {
            global: {
                branches: 0,
                functions: 0,
                lines: 0,
                statements: 0,
            },
        },

        errorOnDeprecated: true,

        moduleFileExtensions: ['ts', 'tsx', 'js', 'json'],

        testEnvironment: 'jsdom',

        coverageDirectory: 'coverage/',

        roots: ['<rootDir>'],
        testRegex: '(/__tests__/.*|(\\.|/)(test|spec))\\.(ts|tsx)$',

        transform: {
            '^.+\\.(ts|tsx)$': [
                'ts-jest',
                {
                    tsconfig: 'tsconfig.test.json',
                    isolateModules: true,
                    useESM: true,
                },
            ],
        },
    };
};
