import { Meta, StoryObj } from '@storybook/angular';
import { TableComponent } from 'app/Common/components/table/table.component';

// Define the metadata for the component
export default {
  title: 'Components/Table', // Group name in Storybook
  component: TableComponent,
  argTypes: {
    searchTerm: { control: 'text' }, // Search term input
    filterValue: { control: 'select', options: ['all', 'active', 'inactive'] }, // Filter dropdown
    editable: { control: 'boolean' }, // Toggle editability
  },
} as Meta;

// Define individual stories
type Story = StoryObj<TableComponent>;

// Default story with sample data and columns
export const Default: Story = {
  args: {
    columns: [
      { key: 'id', label: 'ID', sortable: true },
      { key: 'name', label: 'Name', sortable: true },
      { key: 'status', label: 'Status' },
    ],
    data: [
      { id: 1, name: 'John Doe', status: 'Active' },
      { id: 2, name: 'Jane Smith', status: 'Inactive' },
      { id: 3, name: 'Sam Wilson', status: 'Active' },
    ],
    filterConfig: {
      column: 'status',
      options: [
        { value: 'all', label: 'All' },
        { value: 'active', label: 'Active' },
        { value: 'inactive', label: 'Inactive' },
      ],
    },
    editable: true, // Enable edit functionality
  },
};

// Story with no data (empty table)
export const EmptyTable: Story = {
  args: {
    columns: [
      { key: 'id', label: 'ID', sortable: true },
      { key: 'name', label: 'Name', sortable: true },
      { key: 'status', label: 'Status' },
    ],
    data: [],
    filterConfig: {
      column: 'status',
      options: [
        { value: 'all', label: 'All' },
        { value: 'active', label: 'Active' },
        { value: 'inactive', label: 'Inactive' },
      ],
    },
    editable: true,
  },
};

// Story with a large dataset to test pagination
export const LargeDataset: Story = {
  args: {
    columns: [
      { key: 'id', label: 'ID', sortable: true },
      { key: 'name', label: 'Name', sortable: true },
      { key: 'status', label: 'Status' },
    ],
    data: Array.from({ length: 50 }, (_, i) => ({
      id: i + 1,
      name: `User ${i + 1}`,
      status: i % 2 === 0 ? 'Active' : 'Inactive',
    })),
    filterConfig: {
      column: 'status',
      options: [
        { value: 'all', label: 'All' },
        { value: 'active', label: 'Active' },
        { value: 'inactive', label: 'Inactive' },
      ],
    },
    editable: true,
  },
};

// Story with non-editable table
export const NonEditableTable: Story = {
  args: {
    columns: [
      { key: 'id', label: 'ID', sortable: true },
      { key: 'name', label: 'Name', sortable: true },
      { key: 'status', label: 'Status' },
    ],
    data: [
      { id: 1, name: 'John Doe', status: 'Active' },
      { id: 2, name: 'Jane Smith', status: 'Inactive' },
      { id: 3, name: 'Sam Wilson', status: 'Active' },
    ],
    filterConfig: {
      column: 'status',
      options: [
        { value: 'all', label: 'All' },
        { value: 'active', label: 'Active' },
        { value: 'inactive', label: 'Inactive' },
      ],
    },
    editable: false, // Disable edit functionality
  },
};
