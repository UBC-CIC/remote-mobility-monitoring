export function formResourceName(resourceName: string, stageName: string) {
  return `${resourceName}-${stageName}`;
}