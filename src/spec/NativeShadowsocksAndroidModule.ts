import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

type ShadowsocksProfile = {
  url: string;
};

export interface Spec extends TurboModule {
  importProfileUri(uri: string): ShadowsocksProfile[];

  addProfile(profile: ShadowsocksProfile): number;

  listAllProfile(): ShadowsocksProfile[];

  deleteProfile(profileId: number): void;

  clearProfiles(): void;

  connect(): Promise<boolean>;

  disconnect(): Promise<boolean>;

  switchProfile(profileId: number): number;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ShadowsocksAndroid');
