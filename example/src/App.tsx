import { useState } from 'react';
import { Button, StatusBar, useColorScheme } from 'react-native';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';

import { Profile, Shadowsocks } from 'react-native-shadowsocks-android';

export default function App() {
    const isDarkMode = useColorScheme() === 'dark';
    const [profileID, setProfileID] = useState<number>(0);

    return (
        <SafeAreaProvider>
            <SafeAreaView>
                <StatusBar
                    barStyle={isDarkMode ? 'light-content' : 'dark-content'}
                />
                <Button
                    title={'Connect'}
                    onPress={() =>
                        Shadowsocks.connect().then((a) => console.log(a))
                    }
                />
                <Button
                    title={'Disconnect'}
                    onPress={() => Shadowsocks.disconnect()}
                />
                <Button
                    title={'Switch Profile'}
                    onPress={() => console.log(Shadowsocks.switchProfile(1))}
                />
                <Button
                    title={'Add Profile'}
                    onPress={() => {
                        const profile = new Profile(
                            'us.vpn.cocomine.cc',
                            6381,
                            'BhcW64mXV84TeZW+E5UgAFB/DbGAcx1x2Ljm4CS8iYI=',
                            'aes-256-gcm'
                        );
                        profile.remoteDns = '1.1.1.1';
                        profile.proxyApps = false;
                        profile.individual = [
                            'com.eg.android.AlipayGphone',
                            'com.wudaokou.hippo',
                        ];

                        const id = Shadowsocks.addProfile(profile);
                        console.log(profile);
                        setProfileID(id);
                    }}
                />
                <Button
                    title={'import Profile Uri'}
                    onPress={() => {
                        const profiles = Shadowsocks.importProfileUri(
                            'ss://YWVzLTI1Ni1nY206aUxCS3NQWWdqQ1pnQlpzVE82V3NBVnBiNy96d2E1bjBGV2VsV3YrdE5OWT0=@us2.vpn.cocomine.cc:6381#shadowsocks-app'
                        );

                        console.log(profiles);
                    }}
                />
                <Button
                    title={'Delete Profile'}
                    onPress={() =>
                        console.log(Shadowsocks.deleteProfile(profileID))
                    }
                />
                <Button
                    title={'Clear Profiles'}
                    onPress={() => Shadowsocks.clearProfiles()}
                />
                <Button
                    title={'List All Profiles'}
                    onPress={() => console.log(Shadowsocks.listAllProfile())}
                />
                <Button
                    title={'Get Profiles'}
                    onPress={() =>
                        console.log(Shadowsocks.getProfile(profileID))
                    }
                />
                <Button
                    title={'Update Profile'}
                    onPress={() => {
                        const profile = new Profile(
                            'hk.vpn.cocomine.cc',
                            6373,
                            'i4tbxhk6uEk-LT$',
                            'xchacha20-ietf-poly1305'
                        );
                        profile.id = profileID;
                        profile.proxyApps = false;
                        profile.name = 'hk vpn(edit)';

                        console.log(Shadowsocks.updateProfile(profile));
                    }}
                />
            </SafeAreaView>
        </SafeAreaProvider>
    );
}
