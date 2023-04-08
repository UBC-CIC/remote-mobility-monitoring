import React from 'react';
import { render } from '@testing-library/react';
import LineGraphbyMetrics from './GraphForMultipatients';

test('renders without crashing', () => {
  render(<LineGraphbyMetrics data={[]} />);
});

test('renders a chart for each metric', () => {
  const data = [
    { patient_name: 'Patient 1', metric_name: 'step_length', metric_value: 10, timestamp: new Date() },
    { patient_name: 'Patient 1', metric_name: 'double_support_time', metric_value: 20, timestamp: new Date() },
    { patient_name: 'Patient 1', metric_name: 'walking_speed', metric_value: 30, timestamp: new Date() }
  ];
  const { container } = render(<LineGraphbyMetrics data={data} />);
  expect(container.querySelectorAll('canvas').length).toEqual(3);
});

test('chart options are correctly set', () => {
    const data = [
      { patient_name: 'Patient 1', metric_name: 'step_length', metric_value: 10, timestamp: new Date() },
      { patient_name: 'Patient 1', metric_name: 'double_support_time', metric_value: 20, timestamp: new Date() },
      { patient_name: 'Patient 1', metric_name: 'walking_speed', metric_value: 30, timestamp: new Date() }
    ];
    const { container } = render(<LineGraphbyMetrics data={data} />);
    expect(container.querySelector('.apexcharts-title-text').textContent).toBe('Metrics Chart');
    expect(container.querySelector('.apexcharts-xaxis-title-text').textContent).toBe('Date');
    expect(container.querySelector('.apexcharts-yaxis-title-text').textContent).toBe(undefined);
  });

test('color of each chart is randomized', () => {
  const data = [
    { patient_name: 'Patient 1', metric_name: 'step_length', metric_value: 10, timestamp: new Date() },
    { patient_name: 'Patient 1', metric_name: 'double_support_time', metric_value: 20, timestamp: new Date() },
    { patient_name: 'Patient 1', metric_name: 'walking_speed', metric_value: 30, timestamp: new Date() }
  ];
  const { container } = render(<LineGraphbyMetrics data={data} />);
  const charts = container.querySelectorAll('.apexcharts-canvas');
  const colors = Array.from(charts).map(chart => chart.style.backgroundColor);
  expect(new Set(colors).size).toBeGreaterThan(1);
});