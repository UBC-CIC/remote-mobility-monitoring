import { render } from '@testing-library/react';
import { buildWalkingSpeedChartTest, getRandomColorTest } from './AllPatients';

describe("buildWalkingSpeedChartTest", () => {
  test("returns a non-empty React component", () => {
    const walkingSpeedData = [{ date: "2022-04-01", walking_speed: 2.5 }];
    const component = buildWalkingSpeedChartTest(walkingSpeedData);
    expect(component.props.children).toBeTruthy();
  });

  test("renders a chart title", () => {
    const walkingSpeedData = [{ date: "2022-04-01", walking_speed: 2.5 }];
    const component = buildWalkingSpeedChartTest(walkingSpeedData);
    const title = component.props.children[0];
    expect(title.props.children).toBe("Walking Speed");
  });
});

