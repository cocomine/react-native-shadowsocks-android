import { NativeModules, Platform } from 'react-native';
import type { Profile } from './Profile';

// @ts-ignore
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const LINKING_ERROR =
  `The package 'react-native-shadowsocks-android' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

let ShadowsocksAndroid: any;

if (isTurboModuleEnabled) {
  ShadowsocksAndroid = require('./spec/NativeShadowsocksAndroidModule').default;
} else {
  if (NativeModules.ShadowsocksAndroid) {
    ShadowsocksAndroid = NativeModules.ShadowsocksAndroid;
  } else {
    ShadowsocksAndroid = new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );
  }
}

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
  individual: string | null;
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

/**
 * Adds a Shadowsocks profile.
 *
 * NOTE: After adding the profile, the profile id will be updated.
 *
 * @param {Profile} profile - The profile to be added.
 * @returns {Profile} - The added profile.
 */
function addProfile(profile: Profile): Profile {
  const SSProfile: ShadowsocksProfileType = {
    id: profile.id,
    name: profile.name,
    host: profile.host,
    remotePort: profile.remotePort,
    password: profile.password,
    method: profile.method,
    route: profile.route,
    remoteDns: profile.remoteDns,
    proxyApps: profile.proxyApps,
    bypass: profile.bypass,
    udpdns: profile.udpdns,
    ipv6: profile.ipv6,
    metered: profile.metered,
    individual: profile.individual.join('\n'),
    plugin: profile.plugin,
    plugin_opts: profile.plugin_opts,
  };

  console.log(ShadowsocksAndroid);
  profile.id = ShadowsocksAndroid.addProfile(SSProfile);
  return profile;
}

function deleteProfile(profileId: number): void {
  return ShadowsocksAndroid.deleteProfile(profileId);
}

function clearProfiles(): void {
  return ShadowsocksAndroid.clearProfiles();
}

function connect(): void {
  return ShadowsocksAndroid.connect();
}

function disconnect(): void {
  return ShadowsocksAndroid.disconnect();
}

function switchProfile(profileId: number): number {
  return ShadowsocksAndroid.switchProfile(profileId);
}

export const Shadowsocks = {
  addProfile,
  deleteProfile,
  clearProfiles,
  connect,
  disconnect,
  switchProfile,
};

export type { RouteType, ShadowsocksProfileType };
