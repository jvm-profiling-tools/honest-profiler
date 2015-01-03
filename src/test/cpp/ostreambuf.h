#ifndef OSTREAMBUF_H
#define OSTREAMBUF_H

// based on http://stackoverflow.com/a/1495582/382079
#include <streambuf>

template <class char_type>
struct ostreambuf : public std::basic_streambuf<char_type, std::char_traits<char_type> >
{
  ostreambuf(char_type *buffer, std::streamsize bufferLength)
  {
    // set the "put" pointer the start of the buffer and record its length.
    this->setp(buffer, buffer + bufferLength);
  }
};

#endif /* OSTREAMBUF_H */
