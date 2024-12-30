import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  addProfile(uri: string): number[];

  deleteProfile(profileId: number): void;

  clearProfiles(): void;

  connect(): void;

  disconnect(): void;

  switchProfile(profileId: number): number;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ShadowsocksAndroid');
