import requests
import os, sys
from bs4 import BeautifulSoup

'''
URL of the archive web-page which provides link to
all video lectures. It would have been tiring to
download each video manually.
In this example, we first crawl the webpage to extract
all the links and then download videos.
'''

# specify the URL of the archive here
archive_url = "http://dl.upload8.net/Serial/The%20Big%20Bang%20Theory/S11/"
download_loc = '/Users/saideepak/Downloads/Bigbangtheory/'


def get_video_links():

    # create response object
    r = requests.get(archive_url)

    # create beautiful-soup object
    soup = BeautifulSoup(r.content, 'html5lib')

    # find all links on web-page
    links = soup.findAll('a')

    # filter the link sending with .mp4
    video_links = [archive_url + link['href']
                   for link in links if link['href'].endswith('480p.hdtv.mkv')]

    return video_links


def download_video_series(video_links):

    for link in video_links:

        '''iterate through all links in video_links
        and download them one by one'''

        # obtain filename by splitting url and getting
        # last string
        file_name = link.split('/')[-1]
        print "Getting file:%s" % file_name
        file_loc = os.path.join(download_loc, file_name)
        print file_loc
        if os.path.exists(file_loc):
            print "File exists already: %s" % file_name
        else:
            # create response object
            response = requests.get(link, stream=True)
            total_length = response.headers.get('content-length')
            print "Downloading file:%s" % file_name
            # download started
            try:
                with open(file_loc, 'wb') as f:
                    downloaded = 0
                    total_length = int(total_length)
                    for chunk in response.iter_content(chunk_size=1024 * 1024):
                        downloaded += len(chunk)
                        if chunk:
                            f.write(chunk)
                            done = int(50 * downloaded / total_length)
                            sys.stdout.write("\r[%s%s%s]" % (
                                '=' * (done-1),'>',' ' * (50 - done)))
                            sys.stdout.flush()
            except Exception as error:
                print error
            sys.stdout.write("\n")
            print "%s downloaded!\n" % file_name

    print "All videos downloaded!"
    return


if __name__ == "__main__":

    # getting all video links
    video_links = get_video_links()
    # print video_links

    # download all videos
    download_video_series(video_links)
