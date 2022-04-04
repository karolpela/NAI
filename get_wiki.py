from urllib.request import urlopen
from bs4 import BeautifulSoup
import re, sys, os

def get_text(language: str, expected_len: int) -> bytes:
    output = ''
    while len(output) < expected_len:
        source = urlopen("https://{0}.wikipedia.org/wiki/Special:Random".format(language)).read()
        soup = BeautifulSoup(source, 'lxml')
        for paragraph in soup.find_all('p'):
            text = ''.join(filter(str.isalpha, str(paragraph.text)))
            output += text.lower()
    return output.encode('ascii', errors='ignore')


def save_to_file(path: str, content: str) -> None:
    with open(path, "w+") as text_file:
        text_file.write("%s" % content)


def main() -> None:
    root_path = "data/articles/"  # path where save
    languages = ['pl', 'de', 'es']  # list of languages


    for i in languages:
        new_path = root_path + i
        if not os.path.exists(new_path):
            os.makedirs(new_path)
        for j in range(10):  # number of file
            t = get_text(i, 150)  # length single file
            save_to_file(("{0}/" + i + "_sample{1}.txt").format(new_path, j), t.decode())


if __name__ == '__main__':
    sys.exit(main())
