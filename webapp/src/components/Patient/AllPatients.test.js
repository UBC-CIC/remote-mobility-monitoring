
import { buildWalkingSpeedChartTest } from './AllPatients';

describe('buildWalkingSpeedChartTest', () => {
  const walkingSpeedData = [    { patient_name: 'Patient A', metric_name: 'walking_speed', metric_value: '3.5', timestamp: '2022-01-01T12:00:00Z' },    { patient_name: 'Patient A', metric_name: 'walking_speed', metric_value: '4.2', timestamp: '2022-01-02T12:00:00Z' },    { patient_name: 'Patient B', metric_name: 'walking_speed', metric_value: '2.8', timestamp: '2022-01-01T12:00:00Z' },    { patient_name: 'Patient B', metric_name: 'step_count', metric_value: '1200', timestamp: '2022-01-02T12:00:00Z' },  ];

  it('should render a chart with a title', () => {
    const chart = buildWalkingSpeedChartTest(walkingSpeedData);
    expect(chart.props.children[0].props.children).toBe('Walking Speed');
  });

  it('should render a chart with height of 300px', () => {
    const chart = buildWalkingSpeedChartTest(walkingSpeedData);
    expect(chart.props.height).toBe(300);
  });

  it('should render a chart with an x-axis label of "Date"', () => {
    const chart = buildWalkingSpeedChartTest(walkingSpeedData);
    expect(chart.props.options.xaxis.title.text).toBe('Date');
  });

  it('should render a chart with a minimum y-axis label width of 40', () => {
    const chart = buildWalkingSpeedChartTest(walkingSpeedData);
    expect(chart.props.options.yaxis.labels.minWidth).toBe(40);
  });

  it('should render a chart with x-axis labels rotated -45 degrees', () => {
    const chart = buildWalkingSpeedChartTest(walkingSpeedData);
    expect(chart.props.options.xaxis.labels.rotate).toBe(-45);
  });

  it('should render a chart with walking speed data series', () => {
    const chart = buildWalkingSpeedChartTest(walkingSpeedData);
    expect(chart.props.series.some((s) => s.name === 'Patient A')).toBe(true);
    expect(chart.props.series.some((s) => s.name === 'Patient B')).toBe(true);
  });

  it('should render a chart with different colors for each walking speed data series', () => {
    const chart = buildWalkingSpeedChartTest(walkingSpeedData);
    const colors = chart.props.colors;
    const numColors = colors.length;
    expect(numColors).toBe(2);
    expect(colors[0]).not.toBe(colors[1]);
  });

  it('should render a chart with walking speed data points transformed to x,y coordinates', () => {
    const chart = buildWalkingSpeedChartTest(walkingSpeedData);
    const series = chart.props.series;
    expect(series[0].data[0].x).toBeDefined();
    expect(series[0].data[0].y).toBeDefined();
    expect(series[1].data[0].x).toBeDefined();
    expect(series[1].data[0].y).toBeDefined();
  });
});
