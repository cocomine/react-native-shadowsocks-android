import { NativeModules, Platform } from 'react-native';

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

function addProfile(uri: string): number[] {
  return ShadowsocksAndroid.addProfile(uri);
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
