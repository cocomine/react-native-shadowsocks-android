import type { RouteType } from './Shadowsocks';

/**
 * The profile class.
 * @link: https://github.com/shadowsocks/shadowsocks-android/blob/master/.github/doc-json.md
 */
export class Profile {
  /**
   * The unique identifier for the profile.
   * @type {number}
   */
  id: number = 0;

  /**
   * The name of the profile.
   * (user configurable fields)
   * @type {string | null}
   */
  name: string | null = '';

  /**
   * The host address for the Shadowsocks server.
   * **(Required)**
   * @type {string}
   */
  host: string = 'example.shadowsocks.org';

  /**
   * The remote port number for the Shadowsocks server.
   * **(Required)**
   * @type {number}
   */
  remotePort: number = 8388;

  /**
   * The password for the Shadowsocks server.
   * **(Required)**
   * @type {string}
   */
  password: string = 'password';

  /**
   * The encryption method used by the Shadowsocks server.
   * **(Required)**
   * @type {string}
   */
  method: string = 'aes-256-cfb';

  /**
   * The routing strategy for the profile.
   * @type {RouteType}
   */
  route: RouteType = 'all';

  /**
   * The remote DNS server address.
   * @type {string}
   */
  remoteDns: string = 'dns.google';

  /**
   * Whether to proxy applications.
   * @type {boolean}
   */
  proxyApps: boolean = false;

  /**
   * If `proxyApps` is true,
   * whether the app specified should be bypassed or proxied.
   * @type {boolean}
   */
  bypass: boolean = false;

  /**
   * If `proxyApps` is true,
   * An array of strings, specifying a list of Android app [package names](https://developer.android.com/studio/build/application-id).
   * @type {string}
   */
  individual: string[] = [];

  /**
   * Whether to use UDP for DNS.
   * @type {boolean}
   */
  udpdns: boolean = false;

  /**
   * Whether to use IPv6.
   * @type {boolean}
   */
  ipv6: boolean = false;

  /**
   * Whether the connection is metered.
   * @type {boolean}
   */
  metered: boolean = false;

  /**
   * The plugin used by the profile.
   * shadowsocks-android plugin ID or [alias](https://github.com/shadowsocks/shadowsocks-android/pull/2431).
   * @type {string | null}
   */
  plugin: string | null = null;

  /**
   * If `plugin` is not null,
   * Plugin options [as specified in shadowsocks.org](https://shadowsocks.org/en/spec/Plugin.html).
   * @type {string | null}
   */
  plugin_opts: string | null = null;

  /**
   * Constructs a new Profile instance.
   *
   * @param {string} host - The host address for the Shadowsocks server.
   * @param {number} remotePort - The remote port number for the Shadowsocks server.
   * @param {string} password - The password for the Shadowsocks server.
   * @param {string} method - The encryption method used by the Shadowsocks server.
   * @param {string} [name] - The name of the profile (optional).
   */
  constructor(
    host: string,
    remotePort: number,
    password: string,
    method: string,
    name?: string
  ) {
    name && (this.name = name);
    this.host = host;
    this.remotePort = remotePort;
    this.password = password;
    this.method = method;
  }

  toString(): string {
    return JSON.stringify(this);
  }
}
