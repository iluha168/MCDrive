BUILD_PATH  := dist/
TARGET		:= $(addprefix $(BUILD_PATH), busefifo)
TARGET_O    := $(TARGET:=.o)
LIBOBJS 	:= $(addprefix $(BUILD_PATH), buse.o)
OBJS		:= $(TARGET_O) $(LIBOBJS)
STATIC_LIB	:= $(addprefix $(BUILD_PATH), libbuse.a)

CC		:= gcc
override CFLAGS += -g -pedantic -Wall -Wextra -std=c23
LDFLAGS	:= -L$(BUILD_PATH) -lbuse

.PHONY: all debug clean
all: $(TARGET)

debug: CFLAGS += -DDEBUG -g
debug: all

$(TARGET): %: %.o $(STATIC_LIB) $(BUILD_PATH)
	$(CC) -o $@ $< $(LDFLAGS)

$(TARGET_O): $(BUILD_PATH)%.o: %.c buse.h $(BUILD_PATH)
	$(CC) $(CFLAGS) -o $@ -c $<

$(STATIC_LIB): $(LIBOBJS) $(BUILD_PATH)
	ar rcD $(STATIC_LIB) $(LIBOBJS)

$(LIBOBJS): $(BUILD_PATH)%.o: %.c $(BUILD_PATH)
	$(CC) $(CFLAGS) -o $@ -c $<

$(BUILD_PATH):
	mkdir -p $(BUILD_PATH)

clean:
	rm -rf $(BUILD_PATH)
