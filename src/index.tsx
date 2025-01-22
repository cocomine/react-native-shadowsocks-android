/*import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-shadowsocks-android' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ShadowsocksAndroid = NativeModules.ShadowsocksAndroid
  ? NativeModules.ShadowsocksAndroid
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function addProfile(uri: string): number[] {
  return ShadowsocksAndroid.addProfile(uri);
}

export function deleteProfile(profileId: number): void {
  return ShadowsocksAndroid.deleteProfile(profileId);
}

export function clearProfiles(): void {
  return ShadowsocksAndroid.clearProfiles();
}

export function connect(): void {
  return ShadowsocksAndroid.connect();
}

export function disconnect(): void {
  return ShadowsocksAndroid.disconnect();
}

export function switchProfile(profileId: number): number {
  return ShadowsocksAndroid.switchProfile(profileId);
}*/
import { Shadowsocks } from './Shadowsocks';
import { Profile } from './Profile';

export { Shadowsocks, Profile };
