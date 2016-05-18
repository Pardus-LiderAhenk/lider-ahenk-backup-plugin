#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author: 

class Backup(AbstractCommand):
    def __init__(self, profile_data, context):
        print('init backup...')
        self.profile_data = profile_data
        self.context = context

    def handle_policy(self):
        print('Handling policy...')
        # TODO working...
        pass


def handle_policy(profile_data, context):
    print("This is backup plugin")
    print('Data: {}'.format(str(profile_data)))
    backup = Backup(profile_data, context)
    backup.handle_policy()
