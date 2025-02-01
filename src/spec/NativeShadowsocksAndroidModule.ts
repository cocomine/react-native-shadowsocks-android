import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

/**
 * Represents a Shadowsocks profile type.
 */
type ShadowsocksProfileType = {
  readonly id: number;
  name: string | null;
  host: string;
  remotePort: number;
  password: string;
  method: string;

  route: RouteType;
  remoteDns: string;
  proxyApps: boolean;
  bypass: boolean;
  udpdns: boolean;
  ipv6: boolean;

  metered: boolean;
  individual: string[];
  plugin: string | null;
  plugin_opts: string | null;
};

/**
 * Represents the routing strategy for the profile.
 * @typedef {('all' | 'bypass-lan' | 'bypass-china' | 'bypass-lan-china' | 'gfwlist' | 'china-list')} RouteType
 */
type RouteType =
  | 'all'
  | 'bypass-lan'
  | 'bypass-china'
  | 'bypass-lan-china'
  | 'gfwlist'
  | 'china-list';

export interface Spec extends TurboModule {
  importProfileUri(uri: string): ShadowsocksProfileType[];

  addProfile(ShadowsocksProfile: ShadowsocksProfileType): number;

  listAllProfile(): ShadowsocksProfileType[];

  deleteProfile(profileId: number): boolean;

  clearProfiles(): void;

  getProfile(profileId: number): ShadowsocksProfileType | null;

  updateProfile(profile: ShadowsocksProfileType): boolean;

  connect(): Promise<boolean>;

  disconnect(): void;

  switchProfile(profileId: number): ShadowsocksProfileType;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ShadowsocksAndroid');
