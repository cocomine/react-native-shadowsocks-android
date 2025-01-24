import { NativeModules, Platform } from 'react-native';
import { Profile } from './Profile';

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

/**
 * Adds a Shadowsocks profile.
 *
 * NOTE: After adding the profile, the id attribute in the given profile object will be modified.
 *
 * @param {Profile} profile - The profile to be added.
 * @returns {Profile} - The added profile id.
 */
function addProfile(profile: Profile): number {
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
    individual: profile.individual,
    plugin: profile.plugin,
    plugin_opts: profile.plugin_opts,
  };

  console.log(ShadowsocksAndroid);
  profile.id = ShadowsocksAndroid.addProfile(SSProfile);
  return profile.id;
}

/**
 * Imports profiles from a given URI.
 *
 * @param {string} uri - The URI to import profiles from.
 * @returns {Profile[]} - An array of imported profiles.
 */
function importProfileUri(uri: string): Profile[] {
  const profiles: ShadowsocksProfileType[] =
    ShadowsocksAndroid.importProfileUri(uri);

  return profiles.map((profile) => {
    const profileClass = new Profile(
      profile.host,
      profile.remotePort,
      profile.password,
      profile.method,
      profile.route
    );
    profileClass.id = profile.id;
    profileClass.name = profile.name;
    profileClass.remoteDns = profile.remoteDns;
    profileClass.proxyApps = profile.proxyApps;
    profileClass.bypass = profile.bypass;
    profileClass.udpdns = profile.udpdns;
    profileClass.ipv6 = profile.ipv6;
    profileClass.metered = profile.metered;
    profileClass.individual = profile.individual;
    profileClass.plugin = profile.plugin;
    profileClass.plugin_opts = profile.plugin_opts;

    return profileClass;
  });
}

/**
 * Deletes a Shadowsocks profile by its ID.
 *
 * @param {number} profileId - The ID of the profile to be deleted.
 * @returns {boolean} - Returns true if the profile was successfully deleted, false otherwise.
 */
function deleteProfile(profileId: number): boolean {
  return ShadowsocksAndroid.deleteProfile(profileId);
}

/**
 * Clears all Shadowsocks profiles.
 *
 * @returns {void}
 */
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
  importProfileUri,
};

export type { RouteType, ShadowsocksProfileType };
