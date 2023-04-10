import { render } from '@testing-library/react';
import { transformData, LineGraphbyMetrics } from './GraphForMultipatients';

describe('transformData', () => {
  test('transforms data correctly', () => {
    const data = [
      {
        patient_name: 'John',
        metric_name: 'weight',
        metric_value: '70',
        timestamp: '2022-01-01T00:00:00Z',
      },
      {
        patient_name: 'John',
        metric_name: 'height',
        metric_value: '180',
        timestamp: '2022-01-02T00:00:00Z',
      },
      {
        patient_name: 'Jane',
        metric_name: 'weight',
        metric_value: '60',
        timestamp: '2022-01-01T00:00:00Z',
      },
      {
        patient_name: 'Jane',
        metric_name: 'height',
        metric_value: '170',
        timestamp: '2022-01-02T00:00:00Z',
      },
    ];

    const transformedData = transformData(data, 'weight');

    expect(transformedData).toEqual([
      {
        name: 'John',
        data: [
          { x: 1640995200000, y: 70 },
        ],
      },
      {
        name: 'Jane',
        data: [
          { x: 1640995200000, y: 60 },
        ],
      },
    ]);
  });
});

