/*
 * busefifo - FIFO-based NBD
 * Copyright (C) 2025 iluha168
 *
 * This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
#include <err.h>
#include <argp.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>

#include "buse.h"

#if DEBUG
  #define KEEPLOG(name) name
  #define LOG(...) fprintf(stderr, ##__VA_ARGS__)
#else
  #define KEEPLOG(name)
  #define LOG(...)
#endif

static const char *fifo_request_write_path = "buse_requests_write";
static const char *fifo_request_read_path = "buse_requests_read";

static int fifo_request_read(void *buf, u_int32_t len, u_int64_t offset, void*)
{
  LOG("Read req:\toffset: %lu\tsize: %u\n", offset, len);
  // Request data
  FILE *fifo_request_read = fopen(fifo_request_read_path, "w");
  fwrite(&offset, sizeof(offset), 1, fifo_request_read);
  fwrite(&len, sizeof(len), 1, fifo_request_read);
  fclose(fifo_request_read);
  // Read request response
  fifo_request_read = fopen(fifo_request_read_path, "r");
  fread(buf, len, 1, fifo_request_read);
  fclose(fifo_request_read);
  return 0;
}

static int fifo_request_write(const void *buf, u_int32_t len, u_int64_t offset, void*)
{
  LOG("Write req:\toffset: %lu\tsize: %u\n", offset, len);
  // Send data
  FILE *fifo_request_write = fopen(fifo_request_write_path, "w");
  fwrite(&offset, sizeof(offset), 1, fifo_request_write);
  fwrite(buf, len, 1, fifo_request_write);
  fclose(fifo_request_write);
  // Read write ack
  fifo_request_write = fopen(fifo_request_write_path, "r");
  u_int8_t ack;
  fread(&ack, sizeof(ack), 1, fifo_request_write);
  fclose(fifo_request_write);
  return ack;
}

static void fifo_disc(void*)
{
  LOG("Received a disconnect request.\n");
  remove(fifo_request_write_path);
  remove(fifo_request_read_path);
}

static int fifo_flush(void*)
{
  LOG("Received a flush request.\n");
  return 0;
}

static int fifo_trim(u_int64_t KEEPLOG(from), u_int32_t KEEPLOG(len), void*)
{
  LOG("Trim\toffset: %lu\tsize: %u\n", from, len);
  return 0;
}

/* argument parsing using argp */
static struct argp_option options[] = {
  {0},
};

struct arguments
{
  char *device;
};

/* Parse a single option. */
static error_t parse_opt(int key, char *arg, struct argp_state *state)
{
  struct arguments *arguments = state->input;

  switch (key)
  {
  case ARGP_KEY_ARG:
    switch (state->arg_num)
    {
    case 0:
      arguments->device = arg;
      break;

    default:
      return ARGP_ERR_UNKNOWN;
    }
    break;

  case ARGP_KEY_END:
    break;

  default:
    return ARGP_ERR_UNKNOWN;
  }
  return 0;
}

int main(int argc, char *argv[])
{
  struct arguments arguments;
  {
    static struct argp argp = {
        .options = options,
        .parser = parse_opt,
        .args_doc = "DEVICE",
        .doc = "BUSE virtual block device that stores its content in memory.\n"
               "DEVICE is path to block device, for example \"/dev/nbd0\".",
    };
    argp_parse(&argp, argc, argv, 0, 0, &arguments);
  }

  struct buse_operations aop = {
    .read = fifo_request_read,
    .write = fifo_request_write,
    .disc = fifo_disc,
    .flush = fifo_flush,
    .trim = fifo_trim,
    .blksize = 512ul,
    // Total cubes in a world; 1 block = 1 byte
    .size = 60000000ul * 60000000ul * 384ul,
  };

  return buse_main(arguments.device, &aop, NULL);
}
