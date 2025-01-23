import { useState } from 'react';
import { Button, SafeAreaView, StatusBar, useColorScheme } from 'react-native';

import { Colors } from 'react-native/Libraries/NewAppScreen';
import { Profile, Shadowsocks } from 'react-native-shadowsocks-android';

export default function App() {
  const isDarkMode = useColorScheme() === 'dark';
  const [profileID, setProfileID] = useState<number>(0);

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <Button title={'Connect'} onPress={() => Shadowsocks.connect()} />
      <Button title={'Disconnect'} onPress={() => Shadowsocks.disconnect()} />
      <Button
        title={'Add Profile'}
        onPress={() => {
          const profile = new Profile(
            'hk.vpn.cocomine.cc',
            6373,
            'i4tbxhk6uEk-LT$',
            'xchacha20-ietf-poly1305'
          );
          profile.proxyApps = true;
          profile.individual = [
            'com.eg.android.AlipayGphone',
            'com.wudaokou.hippo',
          ];
          profile.plugin = 'v2ray';
          profile.plugin_opts = 'host=www.example.com';

          const id = Shadowsocks.addProfile(profile);
          console.log(profile);
          setProfileID(id);
        }}
      />
      <Button
        title={'import Profile Uri'}
        onPress={() => {
          const profiles = Shadowsocks.importProfileUri(
            'ss://eGNoYWNoYTIwLWlldGYtcG9seTEzMDU6QXhlN0oyYWtHIUBvQmdU@us.vpn.cocomine.cc:6373/#US_vpn \n' +
              'ss://eGNoYWNoYTIwLWlldGYtcG9seTEzMDU6ZXRFWVpWTnJHeWprLV81@tw.vpn.cocomine.cc:6373/#TW_vpn'
          );

          console.log(profiles);
        }}
      />
      <Button
        title={'Delete Profile'}
        onPress={() => console.log(Shadowsocks.deleteProfile(profileID))}
      />
      <Button
        title={'Clear Profiles'}
        onPress={() => Shadowsocks.clearProfiles()}
      />
      <Button
        title={'Switch Profile'}
        onPress={() => Shadowsocks.switchProfile(profileID)}
      />
    </SafeAreaView>
  );
}
