import type { StorybookConfig } from '@storybook/angular';

const config: StorybookConfig = {
  stories: ['../src/**/*.mdx', '../src/**/*.stories.@(js|jsx|ts|tsx)'],
  addons: ['@storybook/addon-essentials', '@storybook/mdx-gfm'],
  framework: { name: '@storybook/angular', options: {} },
  docs: { autodocs: 'tag' },
};

export default config;
