# SOQLUI
![MIT licensed][shield-license]

SOQLをローカルで実行するクライアントツール


## 概要 - Description
ブラウザを起動せずにローカルで実行できるSOQLのクライアントツール。

## 利用方法 - Usage
### インストール - Install
インストーラー「SOQLUI-x.x.exe」を実行してください。

### 実行方法 - Run
アプリケーション「SOQLUI」を実行します。
拡張子「soql」のデフォルトアプリケーションを設定して開きます。

### Check
```sh
firefox http://localhost:8080/my-project &
```
とか

## Hints
### 拡張子 - Extention
拡張子が「soql」の場合、SOQLの領域にファイルの内容が表示されます。
ファイル名を「*.環境名.soql」とすることで、環境名に自動的に接続し、
SOQLを実行します。

### Distribute
`*.zip`にするタスクとかあるなら

### Examples Of Command
コマンドの実行結果の例とか

## Future Releases
今後の方針

## Contribution
1. Fork it
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create new Pull Request

## License
[MIT](LICENSE)

[shield-license]: https://img.shields.io/badge/license-MIT-green.svg
