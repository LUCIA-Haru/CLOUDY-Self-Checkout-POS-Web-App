import { QuantitySelectorComponent } from './../app/Common/quantity-selector/quantity-selector.component';
import { Meta, StoryFn } from '@storybook/angular';

// Define metadata for the component
export default {
  title: 'Components/QuantitySelector', // Where it appears in Storybook
  component: QuantitySelectorComponent, // The component to showcase
  argTypes: {
    count: {
      control: 'number', // Adds an interactive number input in Storybook
      description: 'The initial quantity value', // Documentation
      defaultValue: 0, // Default value for the control
    },
    countChange: {
      action: 'countChanged', // Logs the emitted event in Storybook's "Actions" tab
    },
  },
} as Meta;

// Template for rendering the component
const Template: StoryFn<QuantitySelectorComponent> = (args) => ({
  props: args,
});

// Story 1: Default state
export const Default = Template.bind({});
Default.args = {
  count: 0, // Start with count at 0
};

// Story 2: With an initial count
export const WithInitialCount = Template.bind({});
WithInitialCount.args = {
  count: 5, // Start with count at 5
};

// Story 3: Negative count prevented
export const AtZero = Template.bind({});
AtZero.args = {
  count: 0, // Start at 0 to test decrement behavior
};
